<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">

</head>
<body>
    ${nombre} por favor compre en despegar 
    <form action="purchase" method="POST">
    <input type="hidden" name="nombre" value="${nombre}"> 
    	Monto<input type="text" name="amount">
    	<input type="submit">
    </form>
</html>