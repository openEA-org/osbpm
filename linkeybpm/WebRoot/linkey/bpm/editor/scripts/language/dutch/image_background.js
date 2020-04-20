function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Afbeelding Bron";
    txtLang[1].innerHTML = "Herhalen";
    txtLang[2].innerHTML = "Horizontale Uitlijning";
    txtLang[3].innerHTML = "Verticale Uitlijning";

    var optLang = document.getElementsByName("optLang");
    optLang[0].text = "Herhalen"
    optLang[1].text = "Niet herhalen"
    optLang[2].text = "Herhalen x"
    optLang[3].text = "Herhalen y"
    optLang[4].text = "links"
    optLang[5].text = "midden"
    optLang[6].text = "rechts"
    optLang[7].text = "pixels"
    optLang[8].text = "procent"
    optLang[9].text = "boven"
    optLang[10].text = "midden"
    optLang[11].text = "onder"
    optLang[12].text = "pixels"
    optLang[13].text = "procent"
    
    document.getElementById("btnCancel").value = "annuleren";
    document.getElementById("btnOk").value = " ok ";
    }
function writeTitle()
    {
    document.write("<title>Achtergrond Afbeelding</title>")
    }

