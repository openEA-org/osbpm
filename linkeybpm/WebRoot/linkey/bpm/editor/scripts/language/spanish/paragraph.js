function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Alinear";
    txtLang[1].innerHTML = "sangr&iacute;a";
    txtLang[2].innerHTML = "Espacio entre palabras";
    txtLang[3].innerHTML = "Espacio entre letras";
    txtLang[4].innerHTML = "Interlineado";
    txtLang[5].innerHTML = "May/min";
    txtLang[6].innerHTML = "Espacio en blanco";
    
    document.getElementById("divPreview").innerHTML = "Lorem ipsum dolor sit amet, " +
        "consetetur sadipscing elitr, " +
        "sed diam nonumy eirmod tempor invidunt ut labore et " +
        "dolore magna aliquyam erat, " +
        "sed diam voluptua. At vero eos et accusam et justo " +
        "duo dolores et ea rebum. Stet clita kasd gubergren, " +
        "no sea takimata sanctus est Lorem ipsum dolor sit amet.";

    var optLang = document.getElementsByName("optLang");
    optLang[0].text = "No definido";
    optLang[1].text = "Izquierda";
    optLang[2].text = "Derecha";
    optLang[3].text = "Centrar";
    optLang[4].text = "Justificar";
    optLang[5].text = "No definido";
    optLang[6].text = "1\u00AA may\u00FAsculas";
    optLang[7].text = "may\u00FAsculas";
    optLang[8].text = "Min\u00FAsculas";
    optLang[9].text = "Ninguno";
    optLang[10].text = "No definido";
    optLang[11].text = "No ajustar";
    optLang[12].text = "pre";
    optLang[13].text = "Normal";
    
    document.getElementById("btnCancel").value = "Cancelar";
    document.getElementById("btnApply").value = "Aplicar";
    document.getElementById("btnOk").value = " Aplicar y salir ";  
    }
function writeTitle()
    {
    document.write("<title>Formato parrafo</title>")
    }
