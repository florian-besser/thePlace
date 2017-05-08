// import io from 'socket.io-client';
import {pixelUpdated} from '../actions/';

const socket_url = `ws://localhost:2222/events/`;
const socket = new WebSocket(socket_url);


export const init = (store) => {
    socket.onmessage = (msg) => {
        store.dispatch(pixelUpdated(JSON.parse(msg.data)))
    };
};
