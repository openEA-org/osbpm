function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Tipo";
    txtLang[1].innerHTML = "Nombre";
    txtLang[2].innerHTML = "Valor";

    var optLang = document.getElementsByName("optLang");
    optLang[0].text = "Bot\u00F3n"
    optLang[1].text = "Aceptar"
    optLang[2].text = "Limpiar"
        
    document.getElementById("btnCancel").value = "Cancelar";
    document.getElementById("btnInsert").value = "Insertar";
    document.getElementById("btnApply").value = "Aplicar";
    document.getElementById("btnOk").value = " Aplicar y salir ";
    }
function writeTitle()
    {
    document.write("<title>"+"Bot\u00F3n"+"</title>")
    }