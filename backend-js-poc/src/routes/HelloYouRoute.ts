import {
    HTTP_METHODS_PARTIAL,
    ReplyNoContinue,
    Request,
} from 'hapi';

export const route = {
    method: 'GET' as HTTP_METHODS_PARTIAL,
    path: '/{name}',
    handler(request, reply) {
        reply('Hello, ' + encodeURIComponent(request.params.name) + '!');
    },
};
