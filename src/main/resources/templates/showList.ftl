<head>
    <script src="/dist/common.merged.js"></script>
    <script src="/dist/app.merged.js"></script>
</head>
<body>

<div>
    <table>
        <th>
            [보트앱 목록]
        </th>
        <#list names as i >
            <tr>
                 <td>
                    ${i}
                </td>
            </tr>
        </#list>
    </table>
</div>



</body>