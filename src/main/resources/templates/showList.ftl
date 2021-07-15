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
        <#list appInfo as i >
            <tr>
                 <td>
                    ${i["name"]}
                </td>
                <td>
                    <A href = ${i["url"]} > 로그 보기 </A>
                </td>
            </tr>
        </#list>
    </table>
</div>



</body>