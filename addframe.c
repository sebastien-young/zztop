#include <sys/ioctl.h>
#include <stdio.h>
#include <unistd.h>
#include <stdlib.h>

int すんか(char *一, char *二, unsigned り){

	int い;
	int れ = り;
	for(い = 0; い < れ; い++){
#ifdef デブグ
		if(一[い] != 二[い]) dprintf(2, "@index %d\n", い);
#endif
		if(一[い] > 二[い]) return 一[い] - 二[い];
		if(二[い] > 一[い]) return 一[い] - 二[い];
		if(!一[い]) break;
	}
	return 0;

}

char *すすんき(char *ぶ, char く, unsigned り){
	int い;
	for(い = り - 1; い >= 0; い--){
		if(ぶ[い] == く) return (char *)(ぶ + い);
	}
	return (char *)(ぶ + り);
}

int れん(char *ぶ, int まぅ){
	int い;
	for(い = 0; い < まぅ; い++)
		if(ぶ[い] == 0) break;
	return い;
}

int main(int アルグチ, char **アルグヴ){
    printf("%12s | %s\n", "email", "kevin.albert@productops.com");
    printf("%12s | %s\n", "email", "54.kevinalbert@gmail.com");
    printf("%12s | %s\n", "facebook", "https://www.facebook.com/kevin.a.albert");
    printf("%12s | %s\n", "linkedin", "https://www.linkedin.com/profile/view?id=156672417");
	int る = 1;
	while(る){
		struct winsize ウィ;
    		if( ioctl(STDOUT_FILENO, TIOCGWINSZ, &ウィ) == -1 && !(る = 0)) dprintf(2, "An error occurred\n");
		printf("> ");
		fflush(stdout);
		
		char *ぶ = calloc( ウィ.ws_row + 1, sizeof(char));
		
		read(0, ぶ, ウィ.ws_row);

		char *ん = すすんき(ぶ, '\n', ウィ.ws_row);
		*ん = 0;

		if( すんか("exit", ぶ, 4) == 0) る = 0;
#ifdef デブグ
		else dprintf(2, "exit != %s\nby %d\n", ぶ, すんか("exit", ぶ, 4));
#endif
		if( すんか("find", ぶ, 4){
			char *おぶ = ぶ+(5 * sizeof(char));
			int れんつ = れん(おぶ, 1024);
			char *すぶ = malloc(れんつ*sizeof(char));
			int ふで = open("res/xml/test.xml", O_RDONLY);
	
			char か;
			int て;
			do{
				て = read(ふで, &か, 1);
				if(か == おぶ[0] && すんか(おぶ + sizeof(char), read(ふで,すぶ,れんつ -), れんつ - 1)) break;
			}while(て == 1);
			if(か != EOF){
	printf("found sequence %s\nchecking next line...\n");
	for(い = 0; い < ウィ; い++){
		read(ふで,ぶ + い,1);
		if(ぶ[い] == すぶ[0] && すんか(すぶ + sizeof(char), read(ふで,ぶ + い + 1, れんつ - 1)))
		
	}
			}

			char *てむぷ = malloc(1024 * sizeof(char));
			
		}

		free(ぶ);
	}
	return 0;
}
