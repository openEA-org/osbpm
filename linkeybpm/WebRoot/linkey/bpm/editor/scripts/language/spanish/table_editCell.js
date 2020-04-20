function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Auto Ajustar";
    txtLang[1].innerHTML = "Propiedades";
    txtLang[2].innerHTML = "Estilo";
    txtLang[3].innerHTML = "Ancho";
    txtLang[4].innerHTML = "Ajustar al contenido";
    txtLang[5].innerHTML = "Fijar ancho de celda";
    txtLang[6].innerHTML = "Altura";
    txtLang[7].innerHTML = "Ajustar al contenido";
    txtLang[8].innerHTML = "Fijar alto de celda";
    txtLang[9].innerHTML = "Alinear texto";
    txtLang[10].innerHTML = "Relleno";
    txtLang[11].innerHTML = "Izquierda";
    txtLang[12].innerHTML = "Derecha";
    txtLang[13].innerHTML = "Arriba";
    txtLang[14].innerHTML = "Abajo";
    txtLang[15].innerHTML = "Espacio en blanco";
    txtLang[16].innerHTML = "Fondo";
    txtLang[17].innerHTML = "Vista Previa";
    txtLang[18].innerHTML = "Texto CSS";
    txtLang[19].innerHTML = "Aplicar a";

    document.getElementById("btnPick").value = "Seleccionar";
    document.getElementById("btnImage").value = "Imagen";
    document.getElementById("btnText").value = " Formato Texto ";
    document.getElementById("btnBorder").value = " Estilo Borde ";

    document.getElementById("btnCancel").value = "Cancelar";
    document.getElementById("btnApply").value = "Aplicar";
    document.getElementById("btnOk").value = " Aplicar y salir ";

    var optLang = document.getElementsByName("optLang");
    optLang[0].text = unescape("p%EDxels")
    optLang[1].text = "porcentaje"
    optLang[2].text = unescape("p%EDxels")
    optLang[3].text = "porcentaje"
    optLang[4].text = "no definido"
    optLang[5].text = "Arriba"
    optLang[6].text = "medio"
    optLang[7].text = "Abajo"
    optLang[8].text = "Linea de base"
    optLang[9].text = "sub"
    optLang[10].text = "super"
    optLang[11].text = "Texto arriba"
    optLang[12].text = "texto abajo"
    optLang[13].text = "no definido"
    optLang[14].text = "Izquierda"
    optLang[15].text = "Centro"
    optLang[16].text = "Derecha"
    optLang[17].text = "Justificar"
    optLang[18].text = "no definido"
    optLang[19].text = "No ajustar texto a ventana"
    optLang[20].text = "pre"
    optLang[21].text = "Normal"
    optLang[22].text = "Celda actual"
    optLang[23].text = "Fila actual"
    optLang[24].text = "Columna actual"
    optLang[25].text = "Whole Table"
    }
function getTxt(s)
    {
    switch(s)
        {
        case "Custom Colors": return "Colores personalizados";
        case "More Colors...": return "Mas Colores...";
        default: return "";
        }
    }
function writeTitle()
    {
    document.write("<title>Propiedades Celda</title>")
    }