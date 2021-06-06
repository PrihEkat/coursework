package com.coursework.service;

import com.coursework.model.Counterparty;
import com.coursework.repository.CounterpartyRepository;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**Service of the "Counterparty" entity.*/
@Service
public class CounterpartyService extends AbstractService<Counterparty, CounterpartyRepository> {
    private static final Integer INN_LEGAL_LENGHT = 10;
    private static final Integer INN_INDIVIDUAL_LENGHT = 12;
    private static final Integer CONTROL_COEF_INN = 11;
    private static final Integer CONTROL_COEF_ACC_NUM = 10;

    private static Logger LOGGER;

    private List<Integer> bikBankClient;
    private List<Integer> accountNumberClient;
    private List<Integer> innClientArray;
    private List<Integer> convertedField;
    private Boolean controlNum1;
    private Boolean controlNum2;

    public CounterpartyService(CounterpartyRepository repository) {
        super(repository);
        LOGGER = Logger.getLogger(CounterpartyService.class.getName());
        innClientArray = new ArrayList<>();
        bikBankClient = new ArrayList<>();
        accountNumberClient = new ArrayList<>();
    }

    /**
     * Retrieves a list of сounterparty from the database.
     *
     * @return list of сounterparty.
     */
    @Override
    public List<Counterparty> readAll() {
        return repository.findAll();
    }

    /**
     * Finds an object by id.
     *
     * @return сounterparty.
     */
    public Counterparty read(Long id) {
        return repository.findById(id).get();
    }

    /**
     * Retrieves an сounterparty from the database by id.
     *
     * @return true - if the сounterparty is in the database, false - if the сounterparty is not present in the database.
     */
    public boolean search(Long id) {
        return repository.existsById(id);
    }

    /**
     * Validates fields and creates a record in the database.
     *
     * @param counterparty
     * @return true - if the new object meets the declared conditions.
     */
    @Override
    public boolean creat(Counterparty counterparty) {
        innClientArray = converterField(new BigInteger(counterparty.getInn()));
        bikBankClient = converterField(new BigInteger(counterparty.getBikBank()));
        accountNumberClient = converterField(new BigInteger(counterparty.getAccountNumber()));
        if (checkInn() && checkAccountNumber()) {
            repository.save(counterparty);
            return true;
        }
        return false;
    }

    /**
     * Converts an objects fields from a string to an array.
     *
     * @param field - object field.
     * @return array of string elements.
     */
    private List<Integer> converterField(BigInteger field) {
        convertedField = new ArrayList<>();
        while (field.compareTo(BigInteger.valueOf(0)) > 0) {
            convertedField.add(0, Integer.valueOf(String.valueOf(field.mod(BigInteger.valueOf(10)))));
            field = field.divide(BigInteger.valueOf(10));
        }
        return convertedField;
    }

    /**
     * Performs keying IIN (IIN of the organization - 10 values; IIN individual or individual
     * entrepreneur - 12 values). Each type of TIN has its own verification algorithm.
     */
    private Boolean checkInn() {
        if (innClientArray.size() == INN_LEGAL_LENGHT) {
            controlNum1 = checkControlNumInn(innClientArray.get(innClientArray.size() - 1), Arrays.asList(2, 4, 10, 3, 5, 9, 4, 6, 8, 0));
            if (controlNum1) {
                LOGGER.log(Level.INFO, "INN verification of organizations passed");
            } else {
                LOGGER.log(Level.INFO, "INN verification of organizations failed");
            }
            return controlNum1;
        } else if (innClientArray.size() == INN_INDIVIDUAL_LENGHT) {
            controlNum1 = checkControlNumInn(innClientArray.get(innClientArray.size() - 2), Arrays.asList(7, 2, 4, 10, 3, 5, 9, 4, 6, 8, 0));
            controlNum2 = checkControlNumInn(innClientArray.get(innClientArray.size() - 1), Arrays.asList(3, 7, 2, 4, 10, 3, 5, 9, 4, 6, 8, 0));
            if (controlNum1 && controlNum2) {
                LOGGER.log(Level.INFO, "INN verification of an individual passed");
            } else {
                LOGGER.log(Level.INFO, "INN verification of an individual failed");
            }
            return (controlNum1 && controlNum2);
        }
        return false;
    }

    /**
     * Calculates the check number and compares it with the corresponding sign in the INN.
     *
     * @param verifiedNum      - еhe value in the INN line with which the reconciliation is performed.
     * @param coefficientArray - Weight coefficients and categories INN code.
     */
    private Boolean checkControlNumInn(Integer verifiedNum, List<Integer> coefficientArray) {
        Integer controlSum = 0;
        for (int i = 0; i < coefficientArray.size(); i++) {
            controlSum = innClientArray.get(i) * coefficientArray.get(i) + controlSum;
        }
        Integer controlNum = controlSum % CONTROL_COEF_INN;
        if (controlNum > 9) {
            controlNum = controlNum % 10;
        }
        return verifiedNum.equals(controlNum);
    }

    /**
     * Checks the BIC and account number. The check is carried out for two cases:
     * 1. If the current account is opened in the RCC (7 and 8 characters of the BIK number "0");
     * 2. The account was opened with a credit institution.
     */
    private Boolean checkAccountNumber() {
        List<Integer> newAccountNumberClient = new ArrayList<>(accountNumberClient);
        Boolean flagCheckAccountNumber = false;
        if ((bikBankClient.get(6) == 0) && (bikBankClient.get(7) == 0)) {
            newAccountNumberClient.add(0, 0);
            newAccountNumberClient.add(0, bikBankClient.get(4));
            newAccountNumberClient.add(0, bikBankClient.get(5));
            flagCheckAccountNumber = checkControlNumAccountNumber(newAccountNumberClient, Arrays.asList(7, 1, 3, 7, 1, 3, 7, 1, 3, 7, 1, 3, 7, 1, 3, 7, 1, 3, 7, 1, 3, 7, 1));
            if (flagCheckAccountNumber) {
                LOGGER.log(Level.INFO, "Verification of the correspondent account opened in the RKC passed");
            } else {
                LOGGER.log(Level.INFO, "Verification of the correspondent account opened in the RKC failed");
            }
        } else {
            for (int i = (bikBankClient.size() - 1); i > (bikBankClient.size() - 4); i--) {
                newAccountNumberClient.add(0, bikBankClient.get(i));
            }
            flagCheckAccountNumber = checkControlNumAccountNumber(newAccountNumberClient, Arrays.asList(7, 1, 3, 7, 1, 3, 7, 1, 3, 7, 1, 3, 7, 1, 3, 7, 1, 3, 7, 1, 3, 7, 1));
            if (flagCheckAccountNumber) {
                LOGGER.log(Level.INFO, "Checking the current account opened with a credit institution passed");
            } else {
                LOGGER.log(Level.INFO, "Checking the current account opened with a credit institution failed");
            }
        }
        return flagCheckAccountNumber;
    }

    /**
     * Calculates the check number.
     *
     * @param accountNumberClient - account number with changes.
     * @param coefficientArray    - weights for calculating the checksum.
     */
    private Boolean checkControlNumAccountNumber(List<Integer> accountNumberClient, List<Integer> coefficientArray) {
        Integer controlSum = 0;
        for (int i = 0; i < coefficientArray.size(); i++) {
            controlSum = accountNumberClient.get(i) * coefficientArray.get(i) + controlSum;
        }
        Integer controlNum = controlSum % CONTROL_COEF_ACC_NUM;
        return controlNum == 0;
    }

    @Override
    public void update(Long id, Counterparty counterparty) {
        repository.saveAndFlush(counterparty);
    }

    @Override
    public void delete(Counterparty counterparty) {
        repository.delete(counterparty);
    }
}
