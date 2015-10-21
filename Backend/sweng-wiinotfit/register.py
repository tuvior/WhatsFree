import webapp2


class Register(webapp2.RequestHandler):
            
    def get(self):
        username = self.request.get("user")
        password = self.request.get("password")
        email = self.request.get("email")
       
        if not username or not password or not email:
            self.response.headers['Content-Type'] = 'text/plain; charset=utf-8'
            self.response.write('Your request is invalid')
        else:
            self.response.headers['Content-Type'] = 'text/plain; charset=utf-8'
            self.response.write('You tried to register in as: ' + username + ' with password: ' + password + ' and email: ' + email)


app = webapp2.WSGIApplication([
    ('/register', Register),
], debug=True)