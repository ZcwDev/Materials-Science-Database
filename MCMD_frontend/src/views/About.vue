<template>
  <v-container class="about-container py-8">
    <v-row>
      <v-col cols="12">
        <!-- About MSD Card -->
        <v-card class="rounded-lg elevation-2">
          <v-card-title class="d-flex align-center py-4 px-6 bg-primary">
            <v-icon color="white" class="mr-2">mdi-information</v-icon>
            <span class="text-h5 font-weight-bold white--text">About Materials Science Database</span>
          </v-card-title>
          <v-card-text class="pa-6">
            <p class="text-body-1 mb-6" style="line-height: 1.6">
              The Materials Science Database is a comprehensive resource for researchers and engineers working in the field of materials science and related disciplines.
            </p>
            <p class="text-subtitle-1 font-weight-medium mb-4">
              Our database provides detailed information about various materials, including:
            </p>
            <v-list class="bg-grey-lighten-4 rounded-lg">
              <v-list-item
                v-for="(item, index) in features"
                :key="index"
                class="py-2"
              >
                <template v-slot:prepend>
                  <v-icon color="primary" class="mr-2">{{ item.icon }}</v-icon>
                </template>
                <v-list-item-title class="text-body-1">{{ item.title }}</v-list-item-title>
              </v-list-item>
            </v-list>
          </v-card-text>
        </v-card>

        <!-- Who We Are Card -->
        <v-card class="mt-6 rounded-lg elevation-2">
          <v-card-title class="d-flex align-center py-4 px-6 bg-primary">
            <v-icon color="white" class="mr-2">mdi-account-group</v-icon>
            <span class="text-h5 font-weight-bold white--text">Who We Are</span>
          </v-card-title>
          <v-card-text class="pa-6">
            <div class="d-flex align-center">
              <a 
                href="https://tcmp2.ucas.ac.cn/" 
                target="_blank" 
                class="lab-title text-decoration-none d-inline-flex align-center"
              >
                Theoretical Condensed Matter Physics & Computational Materials Physics Laboratory
                <v-icon 
                  size="small" 
                  class="ml-2"
                >
                  mdi-open-in-new
                </v-icon>
              </a>
            </div>
          </v-card-text>
        </v-card>

        <!-- Contact Us Card -->
        <v-card class="mt-6 rounded-lg elevation-2">
          <v-card-title class="d-flex align-center py-4 px-6 bg-primary">
            <v-icon color="white" class="mr-2">mdi-email</v-icon>
            <span class="text-h5 font-weight-bold white--text">Contact Us</span>
          </v-card-title>
          <v-card-text class="pa-6">
            <v-list class="contact-list">
              <v-list-item
                v-for="(contact, index) in contacts"
                :key="index"
                :class="{ 'mt-4': index > 0 }"
                class="rounded-lg"
              >
                <template v-slot:prepend>
                  <v-avatar color="primary" size="40">
                    <span class="text-h6 white--text">{{ contact.name[0] }}</span>
                  </v-avatar>
                </template>
                <v-list-item-title class="font-weight-medium text-h6 mb-1">
                  {{ contact.name }}
                </v-list-item-title>
                <v-list-item-subtitle>
                  <a 
                    :href="'mailto:' + contact.email" 
                    class="contact-email text-decoration-none d-flex align-center"
                  >
                    <v-icon size="small" class="mr-2">mdi-email</v-icon>
                    {{ contact.email }}
                  </a>
                </v-list-item-subtitle>
              </v-list-item>
            </v-list>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
export default {
  name: 'About',
  data: () => ({
    features: [
      { title: 'Crystal structures and space groups', icon: 'mdi-crystal-ball' },
      { title: 'Material properties and parameters', icon: 'mdi-atom' },
      { title: 'Advanced search capabilities', icon: 'mdi-database-search' },
      { title: 'Structure visualization', icon: 'mdi-eye' },
      { title: 'X-ray diffraction patterns', icon: 'mdi-chart-bell-curve' }
    ],
    contacts: [
    { 
        name: 'C.-W. Zhang',
        email: 'zhangchengwei23@mails.ucas.ac.cn'
      },
      {
        name: 'Z.-Y. Qi',
        email: 'qizhongyuan@ucas.ac.cn'
      },
      {
        name: 'Q.-B Yan',
        email: 'yan@ucas.ac.cn'
      }
    ]
  })
}
</script>

<style scoped>
.about-container {
  max-width: 1200px;
  margin: 0 auto;
}

.v-card {
  transition: transform 0.2s ease-in-out, box-shadow 0.2s ease-in-out;
  border: none !important;
}

.v-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12) !important;
}

.lab-title {
  font-size: 1.5rem;
  font-weight: 500;
  color: inherit;
  transition: all 0.3s ease;
  line-height: 1.4;
}

.lab-title:hover {
  color: var(--v-primary-base);
}

.lab-title .v-icon {
  opacity: 0.7;
  transition: all 0.3s ease;
}

.lab-title:hover .v-icon {
  opacity: 1;
  transform: translateX(2px);
}

.contact-list {
  background: transparent !important;
}

.contact-list .v-list-item {
  transition: all 0.3s ease;
  padding: 16px;
  background: rgba(0, 0, 0, 0.02);
}

.contact-list .v-list-item:hover {
  background: rgba(0, 0, 0, 0.04);
}

.contact-email {
  color: var(--v-primary-base);
  opacity: 0.8;
  transition: all 0.3s ease;
}

.contact-email:hover {
  opacity: 1;
  text-decoration: underline !important;
}

.v-list-item-subtitle {
  margin-top: 4px !important;
}

.bg-primary {
  border-top-left-radius: 8px;
  border-top-right-radius: 8px;
}
</style>
