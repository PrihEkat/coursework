function getIndex(list, id) {
    for (var i = 0; i < list.length; i++ ) {
        if (list[i].id === id) {
            return i;
        }
    }

    return -1;
}


var counterpartyApi = Vue.resource('/counterparty{/id}');

Vue.component('counterparty-form', {
    props: ['counterparties', 'counterpartyAttr'],
    data: function() {
        return {
            id: '',
            name: '',
            inn: '',
            kpp: '',
            accountNumber: '',
            bikBank: ''
        }
    },
    watch: {
        counterpartyAttr: function(newVal, oldVal) {
            this.id = newVal.id;
            this.name = newVal.name;
            this.inn = newVal.inn;
            this.kpp = newVal.kpp;
            this.accountNumber = newVal.accountNumber;
            this.bikBank = newVal.bikBank;
        }
    },
    template:
        '<div>' +
            '<input type="name" placeholder="Write name" v-model="name" />' +
            '<input type="inn" placeholder="Write inn" v-model="inn" maxlength="12" />' +
            '<input type="kpp" placeholder="Write kpp" v-model="kpp" maxlength="9" />' +
            '<input type="accountNumber" placeholder="Write checking account" v-model="accountNumber" maxlength="20" />' +
            '<input type="bikBank" placeholder="Write bik bank" maxlength="9" v-model="bikBank" /> <br>' +
            '<input type="button" value="Save" @click="save" />' +
        '</div>',
    methods: {
        save: function() {
            var counterparty = {
                name: this.name,
                inn: this.inn,
                kpp: this.kpp,
                accountNumber: this.accountNumber,
                bikBank: this.bikBank
            };

            if (this.id) {
                counterpartyApi.update({id: this.id}, counterparty).then(result =>
                    result.json().then(data => {
                        var index = getIndex(this.counterparties, data.id);
                        this.counterparties.splice(index, 1, data);
                        this.id = ''
                        this.name = ''
                        this.inn = ''
                        this.kpp = ''
                        this.accountNumber = ''
                        this.bikBank = ''
                    })
                )
            } else {
                counterpartyApi.save({}, counterparty).then(result =>
                    result.json().then(data => {
                        this.counterparties.push(data);
                        this.name = ''
                        this.inn = ''
                        this.kpp = ''
                        this.accountNumber = ''
                        this.bikBank = ''
                    })
                )
            }
        }
    }
});

Vue.component('counterparty-row', {
    props: ['counterparty', 'editMethod', 'counterparties'],
    template: '<div> <table style = "border: 1px solid grey;">' +
        '<tr><th>id</th><th>Name</th><th>INN</th><th>KPP</th><th>Account number</th><th>Bik</th></tr>' +
        '<tr style = "border: 1px solid grey;"><td><i>({{ counterparty.id }})</i></td> <td>{{ counterparty.name }}</td>' +
        '<td>{{ counterparty.inn }}</td> <td>{{ counterparty.kpp }}</td>' +
        '<td>{{ counterparty.accountNumber }}</td> <td>{{ counterparty.bikBank }}</td>' +
        '<td><input type="button" value="Edit" @click="edit" /></td>' +
            '<td><input type="button" value="X" @click="del" />' +
        '</td></tr>' +
        '</table> </div>',
    methods: {
        edit: function() {
            this.editMethod(this.counterparty);
        },

        del: function() {
            counterpartyApi.remove({id: this.counterparty.id}).then(result => {
                if (result.ok) {
                    this.counterparties.splice(this.counterparties.indexOf(this.counterparty), 1)
                }
            })
        }
    }
});

Vue.component('counterparties-list', {
  props: ['counterparties'],
  data: function() {
    return {
        counterparty: null
    }
  },
  template:
    '<div style="position: relative; width: 300px;">' +
        '<counterparty-form :counterparties="counterparties" :counterpartyAttr="counterparty" />' +
        '<counterparty-row v-for="counterparty in counterparties" :key="counterparty.id" :counterparty="counterparty" ' +
            ':editMethod="editMethod" :counterparties="counterparties" />' +
    '</div>',
  created: function() {
    counterpartyApi.get().then(result =>
        result.json().then(data =>
            data.forEach(counterparty => this.counterparties.push(counterparty))
        )
    )
  },
  methods: {
    editMethod: function(counterparty) {
        this.counterparty = counterparty;
    }
  }
});

var app = new Vue({
  el: '#app',
  template: '<counterparties-list :counterparties="counterparties" />',
  data: {
    counterparties: []
  }
});