function loadTxt()
    {
    document.getElementById("txtLang").innerHTML = "Ajustar texto a ventana";
    document.getElementById("btnCancel").value = "Cancelar";
    document.getElementById("btnApply").value = "Aplicar";
    document.getElementById("btnOk").value = "Aplicar y salir";
    }
function getTxt(s)
    {
    switch(s)
        {
        case "Search":return "Buscar";
        case "Cut":return "Cortar";
        case "Copy":return "Copiar";
        case "Paste":return "Pegar";
        case "Undo":return "Deshacer";
        case "Redo":return "Rehacer";
        default:return "";
        }
    }
function writeTitle()
    {
    document.write("<title>Editor de codigo</title>")
    }
