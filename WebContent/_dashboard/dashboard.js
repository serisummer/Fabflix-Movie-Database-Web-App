$(document).ready(function () {
    $.ajax({
        dataType: "json",
        url: 'api/metadata',
        method: "GET",
        success: (resultData) => handleMetadata(resultData)
    })
})

function handleMetadata(resultData){
    for (let i = 0; i < resultData.length; i++) {
        let table_name = resultData[i].table;
        let data_string = resultData[i].data;
        let rowhtml = "<h4>" + table_name +"</h4>";
        rowhtml += "<table><thead><tr><th>Attribute</th><th>Type</th></tr></thead><tbody>"
        let data = data_string.split(",")
        for (let j = 0; j < data.length; j++) {
            let datasplit = data[j].split(";")
            let attribute = datasplit[0];
            let type = datasplit[1];
            rowhtml += "<tr><th>" + attribute + "</th><th>" + type +"</th></tr>";
        }

        rowhtml += "</tbody></table>"
        console.log(rowhtml);
        $("#metadata-div").append(rowhtml);
    }
};