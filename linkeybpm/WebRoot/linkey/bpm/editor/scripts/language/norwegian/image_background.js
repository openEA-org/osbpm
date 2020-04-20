function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Sti";
    txtLang[1].innerHTML = "Gjenta";
    txtLang[2].innerHTML = "Vannrett justering";
    txtLang[3].innerHTML = "Loddrett justering";

    var optLang = document.getElementsByName("optLang");
    optLang[0].text = "Gjenta"
    optLang[1].text = "Ikke gjentatt"
    optLang[2].text = "Gjenta X"
    optLang[3].text = "Gjenta Y"
    optLang[4].text = "Venstre"
    optLang[5].text = "Senter"
    optLang[6].text = "H\u00F8yre"
    optLang[7].text = "pixels"
    optLang[8].text = "prosent"
    optLang[9].text = "\u00D8verst"
    optLang[10].text = "Midten"
    optLang[11].text = "Nederst"
    optLang[12].text = "pixels"
    optLang[13].text = "prosent"
    
    document.getElementById("btnCancel").value = "Avbryt";
    document.getElementById("btnOk").value = " Ok ";
    }
function writeTitle()
    {
    document.write("<title>Bakgrunnsbilde</title>")
    }