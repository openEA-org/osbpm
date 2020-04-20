function loadTxt()
    {
    
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Passend";
    txtLang[1].innerHTML = "Eigenschappen";
    txtLang[2].innerHTML = "Stijl";
    txtLang[3].innerHTML = "Breedte";
    txtLang[4].innerHTML = "Passend aan inhoud";
    txtLang[5].innerHTML = "Vaste cel breedte";
    txtLang[6].innerHTML = "Hoogte";
    txtLang[7].innerHTML = "Passend aan inhoud";
    txtLang[8].innerHTML = "Vaste cel hoogte";
    txtLang[9].innerHTML = "Tekst Uitlijning";
    txtLang[10].innerHTML = "Opvulling";
    txtLang[11].innerHTML = "Links";
    txtLang[12].innerHTML = "Rechts";
    txtLang[13].innerHTML = "Boven";
    txtLang[14].innerHTML = "Onder";
    txtLang[15].innerHTML = "WitRuimte";
    txtLang[16].innerHTML = "Achtergrond";
    txtLang[17].innerHTML = "Voorbeeld";
    txtLang[18].innerHTML = "CSS Tekst";
    txtLang[19].innerHTML = "Toepassen op";

    document.getElementById("btnPick").value = "Kiezen";
    document.getElementById("btnImage").value = "Afbeelding";
    document.getElementById("btnText").value = " Text Opmaak ";
    document.getElementById("btnBorder").value = " Rand Stijl ";

    document.getElementById("btnCancel").value = "annuleren";
    document.getElementById("btnApply").value = "toepassen";
    document.getElementById("btnOk").value = " ok ";

    var optLang = document.getElementsByName("optLang");
    optLang[0].text = "pixels"
    optLang[1].text = "procent"
    optLang[2].text = "pixels"
    optLang[3].text = "procent"
    optLang[4].text = "Standaard"
    optLang[5].text = "boven"
    optLang[6].text = "midden"
    optLang[7].text = "onder"
    optLang[8].text = "basislijn"
    optLang[9].text = "sub"
    optLang[10].text = "super"
    optLang[11].text = "text-boven"
    optLang[12].text = "text-onder"
    optLang[13].text = "Standaard"
    optLang[14].text = "links"
    optLang[15].text = "midden"
    optLang[16].text = "rechts"
    optLang[17].text = "uitvullen"
    optLang[18].text = "Standaard"
    optLang[19].text = "Geen terugloop"
    optLang[20].text = "pre"
    optLang[21].text = "Normaal"
    optLang[22].text = "Huidige Cel"
    optLang[23].text = "Huidige Rij"
    optLang[24].text = "Huidige Kolom"
    optLang[25].text = "Whole Table"
    }
function getTxt(s)
    {
    switch(s)
        {
        case "Custom Colors": return "Custom Colors";
        case "More Colors...": return "More Colors...";
        default: return "";
        }
    }
function writeTitle()
    {
    document.write("<title>Cel Eigenschappen</title>")
    }