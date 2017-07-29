import { Server } from 'hapi';
import { Sequelize } from 'sequelize-typescript';

import {
    GoodPlugin
} from './plugins';

import {
    HelloWorldRoute,
    HelloYouRoute,
    MembersRoute
} from './routes';

import {
    Member
} from './models';

const sequelize = new Sequelize({
    username: 'luskydive',
    password: 'luskydive',
    name: 'luskydive',
    host: 'localhost',
    dialect: 'postgres',
    pool: {
        max: 5,
        min: 1,
        idle: 10 * 1000,
    }
});

sequelize.addModels([Member]);

const server = new Server();
server.connection({ port: 3000, host: 'localhost' });

server.route(MembersRoute.route);
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
