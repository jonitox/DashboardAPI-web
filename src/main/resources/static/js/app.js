import Vue from 'vue';
import App from '../../vue/App'

$(document).ready(()=>{
new Vue({
    el: '#app',
    data: {
        message: 'Hellos Vue.js!'
    },
    methods: {
        reverseMessage: function () {
            this.message = this.message.split('').reverse().join('')
        }
    },
    render: h=> h(App)
    });
});
