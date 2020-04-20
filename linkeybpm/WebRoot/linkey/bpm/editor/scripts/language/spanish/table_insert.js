function loadTxt()
    {
    var txtLang =  document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Filas";
    txtLang[1].innerHTML = "Espacio";
    txtLang[2].innerHTML = "Columnas";
    txtLang[3].innerHTML = "Relleno";
    txtLang[4].innerHTML = "Bordes";
    txtLang[5].innerHTML = "Juntar";
    
    var optLang = document.getElementsByName("optLang");
    optLang[0].text = "Sin bordes";
    optLang[1].text = "Si";
    optLang[2].text = "No";
    
    document.getElementById("btnCancel").value = "Cancelar";
    document.getElementById("btnInsert").value = "Insertar";
    
    document.getElementById("btnSpan1").value = "unir v";
    document.getElementById("btnSpan2").value = "unir >";
    }
function writeTitle()
    {
    document.write("<title>Insertar Tabla</title>")
    }