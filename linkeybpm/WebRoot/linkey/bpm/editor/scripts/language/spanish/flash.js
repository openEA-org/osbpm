function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Fuente";
    txtLang[1].innerHTML = "Fondo";
    txtLang[2].innerHTML = "Ancho";
    txtLang[3].innerHTML = "Alto";
    txtLang[4].innerHTML = "Calidad";
    txtLang[5].innerHTML = "Alinear";
    txtLang[6].innerHTML = "Bucle";
    txtLang[7].innerHTML = "Si";
    txtLang[8].innerHTML = "No";
    
    txtLang[9].innerHTML = "Class ID";
    txtLang[10].innerHTML = "CodeBase";
    txtLang[11].innerHTML = "PluginsPage";

    var optLang = document.getElementsByName("optLang");
    optLang[0].text = "Baja"
    optLang[1].text = "Alta"
    optLang[2].text = "<No Definido>"
    optLang[3].text = "Izquierda"
    optLang[4].text = "Derecha"
    optLang[5].text = "Arriba"
    optLang[6].text = "Abajo"
    
    document.getElementById("btnPick").value = "Seleccionar";
    
    document.getElementById("btnCancel").value = "Cancelar";
    document.getElementById("btnOk").value = "Aplicar";
    }
function getTxt(s)
    {
    switch(s)
        {
        case "Custom Colors": return "Colors personalizados";
        case "More Colors...": return "Mas Colors...";
        default: return "";
        }
    }
function writeTitle()
    {
    document.write("<title>Insertar Flash</title>")
    }