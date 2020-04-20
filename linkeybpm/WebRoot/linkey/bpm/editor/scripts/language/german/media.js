function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Dateipfad";
    txtLang[1].innerHTML = "Breite";
    txtLang[2].innerHTML = "H&ouml;he"; 
    txtLang[3].innerHTML = "Auto Start";    
    txtLang[4].innerHTML = "Steuerung anzeigen";
    txtLang[5].innerHTML = "Status Bar anzeigen";   
    txtLang[6].innerHTML = "Display anzeigen";
    txtLang[7].innerHTML = "Auto R&uuml;ckspulen";  

    document.getElementById("btnCancel").value = "Abbrechen";
    document.getElementById("btnInsert").value = "Einf\u00FCgen"; //"insert";
    document.getElementById("btnApply").value = "\u00DCbernehmen"; //"apply";
    document.getElementById("btnOk").value = " OK ";
    }
function writeTitle()
    {
    document.write("<title>"+"Medienobjekt einf\u00FCgen"+"</title>")
    }
