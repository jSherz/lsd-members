import { register } from 'good';

import { PluginRegistrationObject } from 'hapi';

export const plugin: PluginRegistrationObject<any> = {
    options: {
        reporters: {
            console: [{
                args: [{
                    log: '*',
                    response: '*',
                }],
                module: 'good-squeeze',
                name: 'Squeeze',
            }, {
                module: 'good-console',
            }, 'stdout'],
        },
    },
    register,
};
