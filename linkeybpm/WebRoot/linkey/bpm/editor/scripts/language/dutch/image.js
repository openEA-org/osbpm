function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Bron";
    txtLang[1].innerHTML = "Alternatieve Tekst";
    txtLang[2].innerHTML = "Afstand";
    txtLang[3].innerHTML = "Uitlijning";
    txtLang[4].innerHTML = "Top";
    txtLang[5].innerHTML = "Afbeelding rand";
    txtLang[6].innerHTML = "Bottom";
    txtLang[7].innerHTML = "Breedte";
    txtLang[8].innerHTML = "Left";
    txtLang[9].innerHTML = "Hoogte";
    txtLang[10].innerHTML = "Right";
    
    var optLang = document.getElementsByName("optLang");
    optLang[0].text = "absOnder";
    optLang[1].text = "absMidden";
    optLang[2].text = "basislijn";
    optLang[3].text = "onder";
    optLang[4].text = "links";
    optLang[5].text = "midden";
    optLang[6].text = "rechts";
    optLang[7].text = "Bovenkanttekst";
    optLang[8].text = "boven";
 
    document.getElementById("btnBorder").value = " Rand Stijl ";
    document.getElementById("btnReset").value = "reset"
    
    document.getElementById("btnCancel").value = "annuleren";
    document.getElementById("btnInsert").value = "invoegen";
    document.getElementById("btnApply").value = "toepassen";
    document.getElementById("btnOk").value = " ok ";
    }
function writeTitle()
    {
    document.write("<title>Afbeelding</title>")
    }