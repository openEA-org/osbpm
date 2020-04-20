function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Color";
    txtLang[1].innerHTML = "Sombra";    
    
    txtLang[2].innerHTML = "Margen";
    txtLang[3].innerHTML = "Izquierda";
    txtLang[4].innerHTML = "derecha";
    txtLang[5].innerHTML = "Encima";
    txtLang[6].innerHTML = "abajo";
    
    txtLang[7].innerHTML = "Espaciado";
    txtLang[8].innerHTML = "Izquierda";
    txtLang[9].innerHTML = "Derecha";
    txtLang[10].innerHTML = "Encima";
    txtLang[11].innerHTML = "Abajo";

    txtLang[12].innerHTML = "Medidas";
    txtLang[13].innerHTML = "Ancho";
    txtLang[14].innerHTML = "Alto";

    var optLang = document.getElementsByName("optLang");
    optLang[0].text = unescape("p%EDxels");
    optLang[1].text = "porcentaje";
    optLang[2].text = unescape("p%EDxels");
    optLang[3].text = "porcentaje";
    
    document.getElementById("btnCancel").value = "Cancelar";
    document.getElementById("btnApply").value = "Aplicar";
    document.getElementById("btnOk").value = " Aplicar y salir ";
    }
function getTxt(s)
    {
    switch(s)
        {
        case "No Border": return "Sin bordes";
        case "Outside Border": return "Borde alrededor";
        case "Left Border": return "Borde izquierda";
        case "Top Border": return "Border encima";
        case "Right Border": return "Borde derecha";
        case "Bottom Border": return "Borde abajo";
        case "Pick": return "Pick"; //"Seleccionar" => exceed the dialog dimension
        case "Custom Colors": return "Colores personalizados";
        case "More Colors...": return "Mas Colores...";
        default: return "";
        }
    }
function writeTitle()
    {
    document.write("<title>Formato bordes</title>")
    }