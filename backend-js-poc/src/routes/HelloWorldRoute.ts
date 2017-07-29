import {
    HTTP_METHODS_PARTIAL,
    ReplyNoContinue,
    Request,
} from 'hapi';

export const route = {
    method: 'GET' as HTTP_METHODS_PARTIAL,
    path: '/',
    handler(request: Request, reply: ReplyNoContinue): void {
        reply('Hello, world!');
    },
};
