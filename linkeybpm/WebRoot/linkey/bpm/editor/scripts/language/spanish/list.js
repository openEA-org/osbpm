function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Numeracion";
    txtLang[1].innerHTML = "Vi&ntilde;etas";
    txtLang[2].innerHTML = "Comenzar por:";
    txtLang[3].innerHTML = "Margen Izquierdo";
    txtLang[4].innerHTML = "Usar imagen- url"
    txtLang[5].innerHTML = "Margen izquierdo";
    
    document.getElementById("btnCancel").value = "Cancelar";
    document.getElementById("btnApply").value = "Aplicar";
    document.getElementById("btnOk").value = " Aplicar y salir "; 
    }
function getTxt(s)
    {
    switch(s)
        {
        case "Please select a list.":return "Por favor. Seleccione una lista";
        default:return "";
        }
    }
function writeTitle()
    {
    document.write("<title>Formato de lista</title>")
    }
