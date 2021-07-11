import Vue from 'vue';
import App from '../../vue/App'
import DeployList from "../../vue/DeployList";

$(document).ready(()=>{
    new Vue({
        el: '#app',
        data:{},
        render: h=> h(App)
        });
    new Vue({
        el: '#deploymentListVue',
        data:{},
        render: h=> h(DeployList)
    });

});
