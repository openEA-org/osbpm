function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Auto ajustar";
    txtLang[1].innerHTML = "Propiedades";
    txtLang[2].innerHTML = "Estilo";
    txtLang[3].innerHTML = "Ancho";
    txtLang[4].innerHTML = "Ajustar al contenido";
    txtLang[5].innerHTML = "Fijar ancho de tabla";
    txtLang[6].innerHTML = "Auto ajustar a ventana";
    txtLang[7].innerHTML = "Alto";
    txtLang[8].innerHTML = "Ajustar al contenido";
    txtLang[9].innerHTML = "Fijar alto de tabla";
    txtLang[10].innerHTML = "Auto ajustar a ventana";
    txtLang[11].innerHTML = "Alinear";
    txtLang[12].innerHTML = "Margen";
    txtLang[13].innerHTML = "Izquierda";
    txtLang[14].innerHTML = "Derecha";
    txtLang[15].innerHTML = "Arriba";
    txtLang[16].innerHTML = "Abajo";    
    txtLang[17].innerHTML = "Bordes";
    txtLang[18].innerHTML = "Juntar";
    txtLang[19].innerHTML = "Fondo";
    txtLang[20].innerHTML = "Espacio Celda";
    txtLang[21].innerHTML = "Relleno Celda";
    txtLang[22].innerHTML = "Texto CSS";

    var optLang = document.getElementsByName("optLang");
    optLang[0].text = unescape("p%EDxels");
    optLang[1].text = "porcentaje"
    optLang[2].text = unescape("p%EDxels");
    optLang[3].text = "porcentaje"
    optLang[4].text = "Izquierda"
    optLang[5].text = "centro"
    optLang[6].text = "derecha"
    optLang[7].text = "sin bordes"
    optLang[8].text = "Si"
    optLang[9].text = "No"

    document.getElementById("btnPick").value="Seleccionar";
    document.getElementById("btnImage").value="Imagen";

    document.getElementById("btnCancel").value = "Cancelar";
    document.getElementById("btnApply").value = "Aplicar";
    document.getElementById("btnOk").value = " Aplicar y salir ";
    }
function getTxt(s)
    {
    switch(s)
        {
        case "Custom Colors": return "Custom Colors";
        case "More Colors...": return "More Colors...";
        default:return "";
        }
    }
function writeTitle()
    {
    document.write("<title>Propiedades Tabla</title>")
    }