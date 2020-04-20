function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Farvepalette";
    txtLang[1].innerHTML = "Farvens navn";
    txtLang[2].innerHTML = "216 websikre farver";
    txtLang[3].innerHTML = "Ny";
    txtLang[4].innerHTML = "Aktuel";
    txtLang[5].innerHTML = "Egne farver";
    
    document.getElementById("btnAddToCustom").value = "Tilf\u00F8j til egne farver";
    document.getElementById("btnCancel").value = " Annuller ";
    document.getElementById("btnRemove").value = " Fjern farve ";
    document.getElementById("btnApply").value = " Opdater ";
    document.getElementById("btnOk").value = " Ok ";
    }
function writeTitle()
    {
    document.write("<title>Farve</title>")
    }