

let socket = new WebSocket("ws://localhost:8080/websocket");

let obj = {
    userName: '',
    userChoice: '',
    sessionStatus: ''
}

socket.onopen = (e) => {
    obj['sessionStatus'] = 'start';
    obj['userName'] = 'Yura';
    socket.send(JSON.stringify(obj));
    console.log("Socket is open");
}

socket.onmessage = (e) => {
    console.log("Message received");
}

document.getElementById("name_button").addEventListener("click", (e) => {
    e.preventDefault();
    console.log("name_button event");
    obj['sessionStatus'] = 'game';
    obj['userName'] = 'Yura';
    obj['userChoice'] = 'paper';
    socket.send(JSON.stringify(obj));
})
