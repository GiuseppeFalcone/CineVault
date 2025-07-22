/**
 * chat.js
 * =========
 * Client-side JavaScript for Cinevault Chat.
 * Handles UI interactions, namespace switching, chat room switching, message sending, typing indicators,
 * and sound notifications.
 */
document.addEventListener('DOMContentLoaded', initChat);

function initChat() {
    // Set up username.
    let username = localStorage.getItem('username');
    if (!username) {
        username = prompt('Please enter your username:') || 'Anonymous';
        localStorage.setItem('username', username);
    }

    // Define state variables.
    let currentNamespace = 'general'; // Default namespace
    let currentChatId = null;
    let socket = io('/' + currentNamespace);

    // Setup initial socket listeners.
    setupSocketListeners();

    // Setup namespace buttons and UI events.
    setupNamespaceSelectorButtons();
    setupUIEvents();

    // Show no-chat message initially
    toggleNoChatMessage(true);

    controlSound();

    // --- Function Definitions ---

    // Toggle the "Select a chat" message
    function toggleNoChatMessage(show) {
        const noChatMessage = document.getElementById('no-chat-message');
        const messagesDiv = document.getElementById('messages');
        const messageForm = document.getElementById('message-form');
        const typingIndicator = document.getElementById('typing-indicator');

        if (show) {
            noChatMessage.style.display = 'block';
            messagesDiv.style.display = 'none';
            messageForm.style.display = 'none';
            typingIndicator.style.display = 'none';
        } else {
            noChatMessage.style.display = 'none';
            messagesDiv.style.display = 'block';
            messageForm.style.display = 'flex';
            typingIndicator.style.display = 'block';
        }
    }

    // Attach all socket event listeners.
    function setupSocketListeners() {
        socket.on('chatListUpdate', (chatList) => {
            updateChatList(chatList);
        });
        socket.on('message', (data) => {
            // Only add messages not sent by the current user.
            if (data.username !== username || data.username !== 'System') {
                addMessage(data);
                // Play sound chime if available.
                document.getElementById('message-chime').play().catch((err) =>
                    console.error('Chime play error:', err)
                );
            }
        });
        socket.on('chatHistory', (messages) => {
            const messagesDiv = document.getElementById('messages');
            messagesDiv.innerHTML = ''; // Clear existing messages.

            // Hide the "Select a chat" message when chat is selected
            toggleNoChatMessage(false);

            messages.forEach((msg) => {
                const isUser = msg.username === username;
                addMessage({
                    username: isUser ? 'You' : msg.username,
                    text: msg.text,
                    timestamp: msg.timestamp,
                    isUser,
                });
            });
        });
        socket.on('typing', (data) => {
            if (data.username !== username) {
                document.getElementById('typing-indicator').textContent = data.username + ' is typing...';
            }
        });
        socket.on('stopTyping', (data) => {
            if (data.username !== username) {
                document.getElementById('typing-indicator').textContent = '';
            }
        });
    }

    // Attach event listeners for namespace selector buttons.
    function setupNamespaceSelectorButtons() {
        const namespaceSelectorBtns = Array.from(document.getElementsByClassName('namespace-btn'));
        namespaceSelectorBtns.forEach(btn => {
            btn.addEventListener('click', () => {
                // Remove active class from all buttons.
                namespaceSelectorBtns.forEach(b => b.classList.remove('active-btn'));
                const ns = btn.dataset.namespace;
                connectToNamespace(ns);
                btn.classList.add('active-btn');
            });
        });
    }

    // Change namespace by disconnecting the current socket and connecting to the new one.
    function connectToNamespace(ns) {
        if (socket) socket.disconnect();
        currentNamespace = ns;
        socket = io('/' + currentNamespace);
        // Reattach socket event listeners on the new connection.
        setupSocketListeners();

        // When changing namespace, reset chat state
        currentChatId = null;
        toggleNoChatMessage(true);
    }

    // Attach UI event listeners (for changing username, new chat, message sending, typing).
    function setupUIEvents() {
        // Change Username.
        const changeUsernameBtn = document.getElementById('change-username-btn');
        changeUsernameBtn.textContent = 'Username: ' + username;
        changeUsernameBtn.addEventListener('click', () => {
            const newUsername = prompt('Enter your new username:', username);
            if (newUsername) {
                username = newUsername;
                localStorage.setItem('username', username);
                changeUsernameBtn.textContent = 'Username: ' + username;
                if (currentChatId) {
                    socket.emit('joinChat', { chatId: currentChatId, username });
                }
            }
        });

        // New Chat.
        const newChatBtn = document.getElementById('new-chat-btn');
        newChatBtn.addEventListener('click', () => {
            const chatName = prompt('Enter chat name:');
            if (chatName) {
                currentChatId = chatName;
                socket.emit('newChat', chatName);
                socket.emit('joinChat', { chatId: currentChatId, username });
                document.getElementById('messages').innerHTML = '';
                toggleNoChatMessage(false);
            }
        });

        // Message sending.
        const messageForm = document.getElementById('message-form');
        const messageInput = document.getElementById('message-input');
        messageForm.addEventListener('submit', (e) => {
            e.preventDefault();
            const messageText = messageInput.value.trim();
            if (messageText && currentChatId) {
                const timestamp = new Date().toLocaleTimeString();
                socket.emit('newMessage', { chatId: currentChatId, username, message: messageText, timestamp });
                addMessage({ username: 'You', text: messageText, timestamp, isUser: true });
                messageInput.value = '';
                socket.emit('stopTyping', { chatId: currentChatId, username });
            }
        });

        // Typing indicator.
        let typing = false;
        let typingTimeout;
        messageInput.addEventListener('input', () => {
            if (!typing && currentChatId) {
                typing = true;
                socket.emit('typing', { chatId: currentChatId, username });
            }
            clearTimeout(typingTimeout);
            typingTimeout = setTimeout(() => {
                typing = false;
                if (currentChatId) socket.emit('stopTyping', { chatId: currentChatId, username });
            }, 1000);
        });
    }

    // Update chat list UI.
    function updateChatList(chatList) {
        const chatListElement = document.getElementById('chat-list');
        chatListElement.innerHTML = ''; // Clear existing list.
        chatList.forEach((chatName) => {
            const li = document.createElement('li');
            li.classList.add('list-group-item', 'chat-item');
            if (chatName === currentChatId) li.classList.add('active-btn');
            li.textContent = chatName;
            li.dataset.chatId = chatName;
            li.addEventListener('click', () => {
                if (chatName === currentChatId) {
                    // If clicking on the current active chat, exit it
                    currentChatId = null;
                    li.classList.remove('active-btn');
                    toggleNoChatMessage(true);
                } else {
                    // Otherwise switch to the clicked chat
                    switchChat(chatName, li);
                }
            });
            chatListElement.appendChild(li);
        });
    }

    // Switch chat rooms.
    function switchChat(chatId, element) {
        currentChatId = chatId;
        // Remove active class from all chat items.
        Array.from(document.getElementsByClassName('chat-item')).forEach(item => item.classList.remove('active-btn'));
        element.classList.add('active-btn');
        socket.emit('joinChat', { chatId: currentChatId, username });
        document.getElementById('messages').innerHTML = '';
        toggleNoChatMessage(false);
    }

    // Append a message to the chat area.
    function addMessage(message) {
        const messagesDiv = document.getElementById('messages');
        // Render system messages as plain centered text.
        if (message.username === 'System') {
            const systemMsg = document.createElement('div');
            systemMsg.classList.add('system-message');
            systemMsg.textContent = message.text + ' (' + message.timestamp + ')';
            messagesDiv.appendChild(systemMsg);
            messagesDiv.scrollTop = messagesDiv.scrollHeight;
            return;
        }
        // Create a message bubble for user messages.
        const bubble = document.createElement('div');
        bubble.classList.add('message-bubble');
        if (message.isUser) bubble.classList.add('user-message');

        const header = document.createElement('div');
        header.classList.add('message-header');
        header.innerHTML = '<strong>' + message.username + '</strong> <small class="text-muted">' + message.timestamp + '</small>';

        const textDiv = document.createElement('div');
        textDiv.classList.add('message-text');
        textDiv.textContent = message.text;

        bubble.appendChild(header);
        bubble.appendChild(textDiv);
        messagesDiv.appendChild(bubble);
        messagesDiv.scrollTop = messagesDiv.scrollHeight;
    }
}

function controlSound(){
    const chimeAudio = document.getElementById('message-chime');
    const toggleChimeBtn = document.getElementById('toggle-chime-btn');
    const chimeIcon = document.getElementById('chime-icon');
    let chimeMuted = false;

    toggleChimeBtn.addEventListener('click', () => {
        chimeMuted = !chimeMuted;
        chimeAudio.muted = chimeMuted;
        chimeIcon.className = chimeMuted ? 'bi bi-volume-mute' : 'bi bi-volume-up';
        toggleChimeBtn.prepend(chimeIcon);
    });
}