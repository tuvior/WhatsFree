import webapp2
import string
import random


class Login(webapp2.RequestHandler):
            
    def get(self):
        username = self.request.get("user")
        password = self.request.get("password")
        
        if not username or not password:
            self.response.headers['Content-Type'] = 'text/plain; charset=utf-8'
            self.response.write('Your request is invalid')
        else:
            cookie = ''.join(random.choice(string.ascii_uppercase + string.ascii_lowercase + string.digits) for _ in range(64))
            self.response.headers['Content-Type'] = 'text/plain; charset=utf-8'
            self.response.write('You tried to log in as: ' + username + ' with password: ' + password + ' your cookie is: ' + cookie)


app = webapp2.WSGIApplication([
    ('/login', Login),
], debug=True)