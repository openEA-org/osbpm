function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Imagen";
    txtLang[1].innerHTML = "Repetir";
    txtLang[2].innerHTML = "Alinear horizontalmente";
    txtLang[3].innerHTML = "Alinear verticalmente";

    var optLang = document.getElementsByName("optLang");
    optLang[0].text = "Repetir"
    optLang[1].text = "No repetir"
    optLang[2].text = "Repetir horizontal"
    optLang[3].text = "Repetir vertical"
    optLang[4].text = "Izquierda"
    optLang[5].text = "Centro"
    optLang[6].text = "Derecha"
    optLang[7].text = unescape("p%EDxels")
    optLang[8].text = "Porcentaje"
    optLang[9].text = "Arriba"
    optLang[10].text = "medio"
    optLang[11].text = "Abajo"
    optLang[12].text = unescape("p%EDxels")
    optLang[13].text = "Porcentaje"
    
    document.getElementById("btnCancel").value = "Cancelar";
    document.getElementById("btnOk").value = "Aceptar";
    }
function writeTitle()
    {
    document.write("<title>Imagen de fondo</title>")
    }

