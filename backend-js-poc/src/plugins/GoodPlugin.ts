import * as bunyan from 'bunyan';
import { register } from 'good';

import { PluginRegistrationObject } from 'hapi';

export const plugin = (level: bunyan.LogLevel): PluginRegistrationObject<any> => {
    return {
        options: {
            reporters: {
                bunyan: [{
                    args: [
                        { ops: '*', response: '*', log: '*', error: '*', request: '*' },
                        {
                            levels: {
                                ops: 'info',
                                request: 'debug',
                                response: 'debug',
                            },
                            logger: bunyan.createLogger({ level, name: 'lsd-api' }),
                        },
                    ],
                    module: 'good-bunyan',
                }],
            },
        },
        register,
    };
};
