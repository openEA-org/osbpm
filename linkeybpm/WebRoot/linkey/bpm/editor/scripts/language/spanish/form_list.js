function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Nombre";
    txtLang[1].innerHTML = "Tama\u00F1o";
    txtLang[2].innerHTML = "Selecci\u00F3n Multiple";
    txtLang[3].innerHTML = "Valores";
    
    document.getElementById("btnAdd").value = "A\u00F1adir";
    document.getElementById("btnUp").value = "Abajo";
    document.getElementById("btnDown").value = "Encima";
    document.getElementById("btnDel").value = "Borrar";
    document.getElementById("btnCancel").value = "Cancelar";
    document.getElementById("btnInsert").value = "Insertar";
    document.getElementById("btnApply").value = "Aplicar";
    document.getElementById("btnOk").value = "Aplicar y salir";
    }
function writeTitle()
    {
    document.write("<title>Lista</title>")
    }