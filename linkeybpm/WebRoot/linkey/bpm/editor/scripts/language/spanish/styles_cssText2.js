function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Vista Previa";
    txtLang[1].innerHTML = "Texto CSS";
    txtLang[2].innerHTML = "Nombre clase";

    document.getElementById("btnCancel").value = "Cancelar";
    document.getElementById("btnApply").value = "Aplicar";
    document.getElementById("btnOk").value = " Aplicar y salir ";
    }
function getTxt(s)
    {
    switch(s)
        {
        case "You're selecting BODY element.":
            return "Seleccione un elemento del cuerpo.";
        case "Please select a text.":
            return "Por favor, seleccione un texto.";
        default:return "";
        }
    }
function writeTitle()
    {
    document.write("<title>Crear CSS</title>")
    }