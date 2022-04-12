<%@ page import="java.util.ArrayList" %>
<%@ page import="edu.cmu.namana.nutrismart.nutrismartserver.Log" %>
<%@ page import="org.json.JSONObject" %><%--
  Created by IntelliJ IDEA.
  User: naman
  Date: 11/13/2021
  Time: 20:42
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <style>
        table, th, td {
            border: 1px solid black;
            border-collapse: collapse;
        }
    </style>
    <title>NutriSmart Logs</title>
</head>
<body>
<h1>
    NutriSmart Logs
</h1>
<br>
<%--Button to jump to /getResults--%>
<form action="${pageContext.request.contextPath}/stats">
    <input type="submit" value="View Analytics"/>
</form>
<%ArrayList<Log> logs = (ArrayList<Log>) request.getAttribute("logs"); %>


<table>
    <tr>
        <th>Timestamp</th>
        <th>IP</th>
        <th>User Agent</th>
        <th>Query</th>
        <th>Foods</th>
        <th>Calories</th>
        <th>Error</th>
        <th>Response Time</th>
    </tr>
    <%for (Log log : logs) {%>
        <tr>
            <td><%=log.getTimestamp()%></td>
            <td><%=log.getIp()%></td>
            <td><%=log.getUserAgent()%></td>
            <td><%=log.getQuery()%></td>
            <td>
                <%if (log.getFoods() == null) {%>
                    <%=""%>
                <%}else {%>
                    <%for (Object obj : log.getFoods()) {%>
                        <%=((JSONObject) obj).getString("food") + "; "%>
                    <%}}%>
            </td>
            <td>
                <%if (log.getCalories() == null) {%>
                <%=""%>
                <%}else {%>
                <%for (Object obj : log.getCalories()) {%>
                    <%=String.format("%.2f",((JSONObject) obj).getDouble("calories")) + "; "%>
                <%}}%>
            </td>
            <td><%=log.getError()%></td>
            <td><%=Integer.toString(log.getResponseTime())%></td>
        </tr>
    <%}%>
</table>
</body>
</html>
