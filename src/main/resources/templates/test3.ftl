<head>
    <script src="/dist/common.merged.js"></script>
    <script src="/dist/app.merged.js"></script>
</head>
<body>

<div id="app">
    <p>{{ message }}</p>
    <button v-on:click="reverseMessage">메시지 뒤집기</button>
    <input v-model="message">
</div>
</body>