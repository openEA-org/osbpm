function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "K\u00E4lla";
    txtLang[1].innerHTML = "Upprepa";
    txtLang[2].innerHTML = "Horisontellt l\u00E4ge";
    txtLang[3].innerHTML = "Vertikalt l\u00E4ge";

    var optLang = document.getElementsByName("optLang");
    optLang[0].text = "Upprepa"
    optLang[1].text = "Upprepa inte"
    optLang[2].text = "Upprepa horisontellt"
    optLang[3].text = "Upprepa vertikalt"
    optLang[4].text = "V\u00E4nster"
    optLang[5].text = "Centrera"
    optLang[6].text = "H\u00F6ger"
    optLang[7].text = "pixlar"
    optLang[8].text = "procent"
    optLang[9].text = "\u00D6verst"
    optLang[10].text = "Mitten"
    optLang[11].text = "Nederst"
    optLang[12].text = "pixlar"
    optLang[13].text = "procent"
    
    document.getElementById("btnCancel").value = "Avbryt";
    document.getElementById("btnOk").value = " OK ";
    }
function writeTitle()
    {
    document.write("<title>Bakgrundsbild</title>")
    }

