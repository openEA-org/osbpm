function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Tipo";
    txtLang[1].innerHTML = "Nombre";
    txtLang[2].innerHTML = "Tama\u00F1o";
    txtLang[3].innerHTML = "Longitud Max.";
    txtLang[4].innerHTML = "N\u00FAm. Líneas";
    txtLang[5].innerHTML = "Valor";

    var optLang = document.getElementsByName("optLang");
    optLang[0].text = "Texto"
    optLang[1].text = "Area de texto"
    optLang[2].text = "Contrase\u00F1a"
    
    document.getElementById("btnCancel").value = "Cancelar";
    document.getElementById("btnInsert").value = "Insertar";
    document.getElementById("btnApply").value = "Aplicar";
    document.getElementById("btnOk").value = "Aplicar y salir";
    }
function writeTitle()
    {
    document.write("<title>Campo de texto</title>")
    }