import { Server } from 'hapi';

import {
    GoodPlugin
} from './plugins';

import {
    HelloWorldRoute,
    HelloYouRoute
} from './routes';

const server = new Server();
server.connection({ port: 3000, host: 'localhost' });

server.route(HelloWorldRoute.route);
server.route(HelloYouRoute.route);

server.register(GoodPlugin.plugin, (err) => {
    if (err) {
        server.log('error', 'Failed to initialise good plugin');
        throw err;
    }

    server.start((err) => {
        if (err) {
            server.log('error', 'Failed to start server');
            throw err;
        }

        server.log('info', 'Server running at: ' + server.info.uri);
    });
});
