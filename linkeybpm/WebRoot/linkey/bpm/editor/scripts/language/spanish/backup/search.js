function loadTxt()
	{
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Buscar";
    txtLang[1].innerHTML = "Reemplazar";
    txtLang[2].innerHTML = "Coincidir Mayusculas y minusculas";
    txtLang[3].innerHTML = "Palabra completa";

    document.getElementById("btnSearch").value = "Buscar siguiente";
    document.getElementById("btnReplace").value = "reemplazar";
    document.getElementById("btnReplaceAll").value = "reemplazar todo";  
    document.getElementById("btnClose").value = "cerrar";
	}
function writeTitle()
	{
	document.write("<title>Buscar y Reemplazar</title>")
	}