function loadTxt()
    {
    var txtLang =  document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Insertar Fila";
    txtLang[1].innerHTML = "Insertar Columna";
    txtLang[2].innerHTML = "Unir Fila";
    txtLang[3].innerHTML = "Unir Columna";
    txtLang[4].innerHTML = "Borrar Fila";
    txtLang[5].innerHTML = "Borrar Columna";

	document.getElementById("btnInsRowAbove").title="Insertar Fila (encima)";
	document.getElementById("btnInsRowBelow").title="Insertar Fila (debajo)";
	document.getElementById("btnInsColLeft").title="Insertar Columna (izquierda)";
	document.getElementById("btnInsColRight").title="Insertar Columna (derecha)";
	document.getElementById("btnIncRowSpan").title="Juntar filas ";
	document.getElementById("btnDecRowSpan").title="Dividir filas";
	document.getElementById("btnIncColSpan").title="Juntar columnas";
	document.getElementById("btnDecColSpan").title="Dividir columnas";
	document.getElementById("btnDelRow").title="Borrar Fila";
	document.getElementById("btnDelCol").title="Borrar Columna";
	document.getElementById("btnClose").value = " Cerrar ";
    }
function getTxt(s)
    {
    switch(s)
        {
        case "Cannot delete column.":
            return "No es posible borrar la columna. La columna contiene celdas unidas con otra columna. Por favor elimine primero la union.";
        case "Cannot delete row.":
            return "No es posible borrar la fila. La fila contiene celdas unidas con otra fila. Por favor elimine primero la union";
        default:return "";
        }
    }
function writeTitle()
    {
    document.write("<title>Tama\u00F1o&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</title>")
    }