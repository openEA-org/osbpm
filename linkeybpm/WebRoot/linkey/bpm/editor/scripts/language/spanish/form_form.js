function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Nombre";
    txtLang[1].innerHTML = "Acci\u00F3n";
    txtLang[2].innerHTML = "Metodo";
        
    document.getElementById("btnCancel").value = "Cancelar";
    document.getElementById("btnInsert").value = "Insertar";
    document.getElementById("btnApply").value = "Aplicar";
    document.getElementById("btnOk").value = "Aplicar y salir";
    }
function writeTitle()
    {
    document.write("<title>Formulario</title>")
    }