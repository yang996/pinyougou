<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
</head>
<body>
<#--<h1>${name}:${message}</h1>-->
<hr/>
<#assign likename="黑马"/>
联系人:${likename}
<br/>
<#assign info={"mobile":"123","address":"吉山村"}/>
联系电话:${info.mobile}---地址:${info.address}
<br/>
<#include "header.ftl"/>
<br/>
<#assign bool=true/>
<#if bool>
    bool的值为真
<#else >
    bool的值为假
</#if>
<br/>
<#list arrayList as item>
    ${item_index}---名称为:${item.name}---地址为:${item.message}<br/>
</#list>
<#assign str='{"id":123,"text":"itcast"}'/>
 <#assign jsonObj=str?eval/>
id为:${jsonObj["id"]}---text为:${jsonObj.text}<br/>
当前日期:${date?date}<br/>
当前时间:${date?time}<br/>
当前日期+时间:${date?datetime}<br/>
格式化显示当前日期时间:${date?string('yyyy-MM-dd')}<br/>
number=${number}<br/>
number=${number?c}<br/>

</body>
</html>