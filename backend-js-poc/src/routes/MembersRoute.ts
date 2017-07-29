import {
    HTTP_METHODS_PARTIAL,
    ReplyNoContinue,
    Request,
} from 'hapi';

import { Member } from '../models';

export const route = {
    method: 'GET' as HTTP_METHODS_PARTIAL,
    path: '/members',
    handler(request: Request, reply: ReplyNoContinue): void {
        Member.findAll().then(reply);
    },
};
