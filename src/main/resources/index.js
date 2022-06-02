

let socket = new WebSocket("ws://localferfrhost:8080/websocket");

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

let chatSocket = new WebSocket("ws://localhost:8080/chat")
let message = {
    userName: "",
    userId: "",
    groupId: "",
    userMessage: "",
    type: "",
    data: ""
}

chatSocket.onopen = (e) => {
    message['userName'] = "Yura"
    message['type'] = "start"
    chatSocket.send(JSON.stringify(message));
    console.log("Socket is open");
    message['userName'] = "Yura"
    message['type'] = "message"
    message['userMessage'] = "privet"
    chatSocket.send(JSON.stringify(message));
}

let groupId;

chatSocket.onmessage = (e) => {
    console.log("Message received");
    const data = JSON.parse(e.data);
    groupId = data['groupId'];
}


let history = [];
document.getElementById("name_button").addEventListener("click", (e) => {
    e.preventDefault();
    console.log("name_button event");
    fetch('http://localhost:8080/message/getHistory/' + groupId)
        .then((response) => {
            return response.json();
        })
        .then((data) => {
            for (var i=0;i<data.length;i++) {
                console.log(data[i]);
            }
        });
})


