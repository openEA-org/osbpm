function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Archivo";
    txtLang[1].innerHTML = "Ancho";
    txtLang[2].innerHTML = "Alto";  
    txtLang[3].innerHTML = "Auto ejecutar"; 
    txtLang[4].innerHTML = "Mostrar controles";
    txtLang[5].innerHTML = "Mostrar barra de estado";   
    txtLang[6].innerHTML = "Mostrar pantalla";
    txtLang[7].innerHTML = "Auto Rebobinar";    

    document.getElementById("btnCancel").value = "Cancelar";
    document.getElementById("btnInsert").value = "Insertar";
    document.getElementById("btnApply").value = "Aaplicar";
    document.getElementById("btnOk").value = "Aplicar y salir";
    }
function writeTitle()
    {
    document.write("<title>Insertar archivo multimedia</title>")
    }
