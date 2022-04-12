<%@ page import="java.util.ArrayList" %>
<%--
  Created by IntelliJ IDEA.
  User: naman
  Date: 11/13/2021
  Time: 03:04
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>NutriSmart Stats</title>
</head>
<body>
<h1>
    NutriSmart Analytics Dashboard
</h1>
<br>
<%--Button to jump to /getResults--%>
<form action="${pageContext.request.contextPath}/logs">
    <input type="submit" value="View Full Logs"/>
</form>

<%ArrayList<String> topFoods = (ArrayList<String>) request.getAttribute("topFoods");%>
<%ArrayList<Integer> topFoodsCount = (ArrayList<Integer>) request.getAttribute("topFoodsCount");%>

<h2>Top Searched Foods</h2>
<%for (int i = 0; i < topFoods.size(); i++) {%>
    <p style="font-size:20px;">
        <%="<i>" + topFoods.get(i) + "</i>: <b>" + topFoodsCount.get(i).toString() + "</b>"%>
    </p>
<%}%>
<br>

<h2>Average Calories Per Meal</h2>
<p style="font-size:20px;">
<%="Each meal entered consisted of an average of <b>" + request.getAttribute("averageCalories") + "</b> calories."%>
</p>
<br>

<h2>Total Queries Served</h2>
<p style="font-size:20px;">
<%="NutriSmart has served a total of <b>" + request.getAttribute("totalQueries").toString() + "</b> queries."%>
</p>
<br>

<h2>Average Response Time</h2>
<p style="font-size:20px;">
    <%="NutriSmart has maintained an average response time of <b>" + request.getAttribute("averageResponseTime").toString() + "</b> ms."%>
</p>
<br>

</body>
</html>
