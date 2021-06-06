package com.coursework.controller;

import com.coursework.model.Counterparty;
import com.coursework.service.CounterpartyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**Controller of the "Counterparty" entity.*/
@RestController
@RequestMapping("/counterparty")
public class CounterpartyController extends AbstractController<Counterparty, CounterpartyService>{

    CounterpartyController(CounterpartyService service) {
        super(service);
    }

    /**
     *Display of all counterparty.
     * @return List of counterparty.
     */
    @GetMapping
    public ResponseEntity<List<Counterparty>> getAll() {
        List<Counterparty> counterparty = service.readAll();
        return new ResponseEntity<List<Counterparty>>(counterparty, HttpStatus.OK);
    }

    /**
     *Creation of a new counterparty.
     * @param counterparty
     * @return new counterparty
     */
    @PostMapping
    public ResponseEntity<Counterparty> create(@RequestBody Counterparty counterparty) {
        Boolean checkCreateCounterparty = service.creat(counterparty);
        if (checkCreateCounterparty){
            return new ResponseEntity<>(counterparty, HttpStatus.CREATED);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    /**
     *Edits an existing сounterparty.
     * @param id - id needed сounterparty.
     * @param counterpartyDetails - mutable сounterparty.
     * @return change сounterparty.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Counterparty> update(@PathVariable Long id,
                                                 @RequestBody Counterparty counterpartyDetails){
        if (service.search(id)){
            service.update(id, counterpartyDetails);
            return new ResponseEntity<Counterparty>(counterpartyDetails, HttpStatus.OK);
        }
        return  new ResponseEntity<Counterparty>(HttpStatus.NOT_FOUND);
    }

    /**
     *Removes a сounterparty by id.
     * @param id - id сounterparty.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        if (service.search(id)){
            Counterparty counterparty = service.read(id);
            service.delete(counterparty);
            return new ResponseEntity<Void>(HttpStatus.OK);
        }
        return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
    }
}

