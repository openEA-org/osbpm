function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Nombre";
    txtLang[1].innerHTML = "Valor";
    txtLang[2].innerHTML = "Estado";

    var optLang = document.getElementsByName("optLang");
    optLang[0].text = "Activado"
    optLang[1].text = "Desactivado"
    
    document.getElementById("btnCancel").value = "Cancelar";
    document.getElementById("btnInsert").value = "Insertar";
    document.getElementById("btnApply").value = "Aplicar";
    document.getElementById("btnOk").value = "Aplicar y salir";
    }
function writeTitle()
    {
    document.write("<title>"+"Casilla de Verificaci\u00F3n"+"</title>")
    }