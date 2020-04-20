function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Nombre";
    txtLang[1].innerHTML = "Valor";
    
    document.getElementById("btnCancel").value = "Cancelar";
    document.getElementById("btnInsert").value = "Insertar";
    document.getElementById("btnApply").value = "Aplicar";
    document.getElementById("btnOk").value = "Aplicar y salir";
    }
function writeTitle()
    {
    document.write("<title>Campo Oculto</title>")
    }
