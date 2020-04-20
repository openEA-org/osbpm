var sStyleWeight1;
var sStyleWeight2;
var sStyleWeight3;
var sStyleWeight4; 

function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Fuente";
    txtLang[1].innerHTML = "Estilo";
    txtLang[2].innerHTML = "Tama\u00F1o";
    txtLang[3].innerHTML = "Color Texto";
    txtLang[4].innerHTML = "Color Fondo";
    
    txtLang[5].innerHTML = "Decoraci\u00F3n";
    txtLang[6].innerHTML = "May./Min.";
    txtLang[7].innerHTML = "Variante";
    txtLang[8].innerHTML = "Alineaci\u00F3n Vertical";

    txtLang[9].innerHTML = "No definido";
    txtLang[10].innerHTML = "Subrallado";
    txtLang[11].innerHTML = "Sobreimpresi\u00F3n";
    txtLang[12].innerHTML = "Tachar";
    txtLang[13].innerHTML = "Ninguno";

    txtLang[14].innerHTML = "No definido";
    txtLang[15].innerHTML = "1&ordf; may&uacute;sculas";
    txtLang[16].innerHTML = "may\u00FAsculas";
    txtLang[17].innerHTML = "Min\u00FAsculas";
    txtLang[18].innerHTML = "Ninguna";

    txtLang[19].innerHTML = "No definido";
    txtLang[20].innerHTML = "Versales";
    txtLang[21].innerHTML = "Normal";

    txtLang[22].innerHTML = "No definido";
    txtLang[23].innerHTML = "Super\u00EDndice";
    txtLang[24].innerHTML = "Sub\u00EDndice";
    txtLang[25].innerHTML = "Relativo";
    txtLang[26].innerHTML = "l\u00EDnea de Base ";
    
    txtLang[27].innerHTML = "Espacio caracteres";

    var optLang = document.getElementsByName("optLang");
    optLang[0].text = "Regular"
    optLang[1].text = "Italica"
    optLang[2].text = "Negrita"
    optLang[3].text = "Negrita Italica"
    
    optLang[0].value = "Regular"
    optLang[1].value = "Italica"
    optLang[2].value = "Negrita"
    optLang[3].value = "Negrita Italica"
    
    sStyleWeight1 = "Regular"
    sStyleWeight2 = "Italica"
    sStyleWeight3 = "Negrita"
    sStyleWeight4 = "Negrita Italica"

    optLang[4].text = "Arriba"
    optLang[5].text = "Medio"
    optLang[6].text = "Abajo"
    optLang[7].text = "Texto-superior"
    optLang[8].text = "Texto-inferior"
    
    document.getElementById("btnPick1").value = "Seleccionar";
    document.getElementById("btnPick2").value = "Seleccionar";

    document.getElementById("btnCancel").value = "Cancelar";
    document.getElementById("btnOk").value = " Aceptar ";
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
    document.write("<title>Formato Texto</title>")
    }