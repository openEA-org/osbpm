function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Hierpvinculo";
    txtLang[1].innerHTML = "Vinculo interno";
    txtLang[2].innerHTML = "Destino";
    txtLang[3].innerHTML = "Titulo";

    var optLang = document.getElementsByName("optLang");
    optLang[0].text = "Self"
    optLang[1].text = "Blank"
    optLang[2].text = "Parent"
    
    document.getElementById("btnCancel").value = "Cancelar";
    document.getElementById("btnInsert").value = "Insertar";
    document.getElementById("btnApply").value = "Aplicar";
    document.getElementById("btnOk").value = "Aplicar y salir";
    }
function writeTitle()
    {
    document.write("<title>Vinculo</title>")
    }