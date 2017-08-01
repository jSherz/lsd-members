import { route } from './HelloYouRoute';

describe('routes/HelloYouRoute', () => {

    it('has the correct method and path', () => {
        expect(route.method).toEqual('GET');
        expect(route.path).toEqual('/{name}');
    });

    it('greets the user', () => {
        const request: any = {
            params: {
                name: 'Foobar',
            },
        };
        const reply: any = jasmine.createSpy('reply');
        route.handler(request, reply);

        expect(reply).toHaveBeenCalledWith('Hello, Foobar!');
    });

});
