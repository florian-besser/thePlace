// import io from 'socket.io-client';
import {pixelsUpdated, API_HOST} from '../actions/';

const SOCKET_URL = `ws://${API_HOST}/events/`;
const socket = new WebSocket(SOCKET_URL);


export const init = (store) => {
    socket.onmessage = (msg) => {
        store.dispatch(pixelsUpdated(JSON.parse(msg.data)))
    };
};
